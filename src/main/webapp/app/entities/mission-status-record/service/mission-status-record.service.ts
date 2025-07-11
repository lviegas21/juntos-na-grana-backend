import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMissionStatusRecord, NewMissionStatusRecord } from '../mission-status-record.model';

export type PartialUpdateMissionStatusRecord = Partial<IMissionStatusRecord> & Pick<IMissionStatusRecord, 'id'>;

type RestOf<T extends IMissionStatusRecord | NewMissionStatusRecord> = Omit<T, 'date'> & {
  date?: string | null;
};

export type RestMissionStatusRecord = RestOf<IMissionStatusRecord>;

export type NewRestMissionStatusRecord = RestOf<NewMissionStatusRecord>;

export type PartialUpdateRestMissionStatusRecord = RestOf<PartialUpdateMissionStatusRecord>;

export type EntityResponseType = HttpResponse<IMissionStatusRecord>;
export type EntityArrayResponseType = HttpResponse<IMissionStatusRecord[]>;

@Injectable({ providedIn: 'root' })
export class MissionStatusRecordService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/mission-status-records');

  create(missionStatusRecord: NewMissionStatusRecord): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(missionStatusRecord);
    return this.http
      .post<RestMissionStatusRecord>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(missionStatusRecord: IMissionStatusRecord): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(missionStatusRecord);
    return this.http
      .put<RestMissionStatusRecord>(`${this.resourceUrl}/${this.getMissionStatusRecordIdentifier(missionStatusRecord)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(missionStatusRecord: PartialUpdateMissionStatusRecord): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(missionStatusRecord);
    return this.http
      .patch<RestMissionStatusRecord>(`${this.resourceUrl}/${this.getMissionStatusRecordIdentifier(missionStatusRecord)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestMissionStatusRecord>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestMissionStatusRecord[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMissionStatusRecordIdentifier(missionStatusRecord: Pick<IMissionStatusRecord, 'id'>): number {
    return missionStatusRecord.id;
  }

  compareMissionStatusRecord(o1: Pick<IMissionStatusRecord, 'id'> | null, o2: Pick<IMissionStatusRecord, 'id'> | null): boolean {
    return o1 && o2 ? this.getMissionStatusRecordIdentifier(o1) === this.getMissionStatusRecordIdentifier(o2) : o1 === o2;
  }

  addMissionStatusRecordToCollectionIfMissing<Type extends Pick<IMissionStatusRecord, 'id'>>(
    missionStatusRecordCollection: Type[],
    ...missionStatusRecordsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const missionStatusRecords: Type[] = missionStatusRecordsToCheck.filter(isPresent);
    if (missionStatusRecords.length > 0) {
      const missionStatusRecordCollectionIdentifiers = missionStatusRecordCollection.map(missionStatusRecordItem =>
        this.getMissionStatusRecordIdentifier(missionStatusRecordItem),
      );
      const missionStatusRecordsToAdd = missionStatusRecords.filter(missionStatusRecordItem => {
        const missionStatusRecordIdentifier = this.getMissionStatusRecordIdentifier(missionStatusRecordItem);
        if (missionStatusRecordCollectionIdentifiers.includes(missionStatusRecordIdentifier)) {
          return false;
        }
        missionStatusRecordCollectionIdentifiers.push(missionStatusRecordIdentifier);
        return true;
      });
      return [...missionStatusRecordsToAdd, ...missionStatusRecordCollection];
    }
    return missionStatusRecordCollection;
  }

  protected convertDateFromClient<T extends IMissionStatusRecord | NewMissionStatusRecord | PartialUpdateMissionStatusRecord>(
    missionStatusRecord: T,
  ): RestOf<T> {
    return {
      ...missionStatusRecord,
      date: missionStatusRecord.date?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restMissionStatusRecord: RestMissionStatusRecord): IMissionStatusRecord {
    return {
      ...restMissionStatusRecord,
      date: restMissionStatusRecord.date ? dayjs(restMissionStatusRecord.date) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestMissionStatusRecord>): HttpResponse<IMissionStatusRecord> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestMissionStatusRecord[]>): HttpResponse<IMissionStatusRecord[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
