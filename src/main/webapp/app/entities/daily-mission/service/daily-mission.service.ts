import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDailyMission, NewDailyMission } from '../daily-mission.model';

export type PartialUpdateDailyMission = Partial<IDailyMission> & Pick<IDailyMission, 'id'>;

type RestOf<T extends IDailyMission | NewDailyMission> = Omit<T, 'startDate' | 'endDate' | 'createdAt'> & {
  startDate?: string | null;
  endDate?: string | null;
  createdAt?: string | null;
};

export type RestDailyMission = RestOf<IDailyMission>;

export type NewRestDailyMission = RestOf<NewDailyMission>;

export type PartialUpdateRestDailyMission = RestOf<PartialUpdateDailyMission>;

export type EntityResponseType = HttpResponse<IDailyMission>;
export type EntityArrayResponseType = HttpResponse<IDailyMission[]>;

@Injectable({ providedIn: 'root' })
export class DailyMissionService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/daily-missions');

  create(dailyMission: NewDailyMission): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dailyMission);
    return this.http
      .post<RestDailyMission>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(dailyMission: IDailyMission): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dailyMission);
    return this.http
      .put<RestDailyMission>(`${this.resourceUrl}/${this.getDailyMissionIdentifier(dailyMission)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(dailyMission: PartialUpdateDailyMission): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dailyMission);
    return this.http
      .patch<RestDailyMission>(`${this.resourceUrl}/${this.getDailyMissionIdentifier(dailyMission)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestDailyMission>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDailyMission[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDailyMissionIdentifier(dailyMission: Pick<IDailyMission, 'id'>): number {
    return dailyMission.id;
  }

  compareDailyMission(o1: Pick<IDailyMission, 'id'> | null, o2: Pick<IDailyMission, 'id'> | null): boolean {
    return o1 && o2 ? this.getDailyMissionIdentifier(o1) === this.getDailyMissionIdentifier(o2) : o1 === o2;
  }

  addDailyMissionToCollectionIfMissing<Type extends Pick<IDailyMission, 'id'>>(
    dailyMissionCollection: Type[],
    ...dailyMissionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const dailyMissions: Type[] = dailyMissionsToCheck.filter(isPresent);
    if (dailyMissions.length > 0) {
      const dailyMissionCollectionIdentifiers = dailyMissionCollection.map(dailyMissionItem =>
        this.getDailyMissionIdentifier(dailyMissionItem),
      );
      const dailyMissionsToAdd = dailyMissions.filter(dailyMissionItem => {
        const dailyMissionIdentifier = this.getDailyMissionIdentifier(dailyMissionItem);
        if (dailyMissionCollectionIdentifiers.includes(dailyMissionIdentifier)) {
          return false;
        }
        dailyMissionCollectionIdentifiers.push(dailyMissionIdentifier);
        return true;
      });
      return [...dailyMissionsToAdd, ...dailyMissionCollection];
    }
    return dailyMissionCollection;
  }

  protected convertDateFromClient<T extends IDailyMission | NewDailyMission | PartialUpdateDailyMission>(dailyMission: T): RestOf<T> {
    return {
      ...dailyMission,
      startDate: dailyMission.startDate?.toJSON() ?? null,
      endDate: dailyMission.endDate?.toJSON() ?? null,
      createdAt: dailyMission.createdAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restDailyMission: RestDailyMission): IDailyMission {
    return {
      ...restDailyMission,
      startDate: restDailyMission.startDate ? dayjs(restDailyMission.startDate) : undefined,
      endDate: restDailyMission.endDate ? dayjs(restDailyMission.endDate) : undefined,
      createdAt: restDailyMission.createdAt ? dayjs(restDailyMission.createdAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestDailyMission>): HttpResponse<IDailyMission> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestDailyMission[]>): HttpResponse<IDailyMission[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
