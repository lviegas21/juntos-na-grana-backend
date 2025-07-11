import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGoal, NewGoal } from '../goal.model';

export type PartialUpdateGoal = Partial<IGoal> & Pick<IGoal, 'id'>;

type RestOf<T extends IGoal | NewGoal> = Omit<T, 'createdAt' | 'dueDate'> & {
  createdAt?: string | null;
  dueDate?: string | null;
};

export type RestGoal = RestOf<IGoal>;

export type NewRestGoal = RestOf<NewGoal>;

export type PartialUpdateRestGoal = RestOf<PartialUpdateGoal>;

export type EntityResponseType = HttpResponse<IGoal>;
export type EntityArrayResponseType = HttpResponse<IGoal[]>;

@Injectable({ providedIn: 'root' })
export class GoalService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/goals');

  create(goal: NewGoal): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(goal);
    return this.http.post<RestGoal>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(goal: IGoal): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(goal);
    return this.http
      .put<RestGoal>(`${this.resourceUrl}/${this.getGoalIdentifier(goal)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(goal: PartialUpdateGoal): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(goal);
    return this.http
      .patch<RestGoal>(`${this.resourceUrl}/${this.getGoalIdentifier(goal)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestGoal>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestGoal[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getGoalIdentifier(goal: Pick<IGoal, 'id'>): number {
    return goal.id;
  }

  compareGoal(o1: Pick<IGoal, 'id'> | null, o2: Pick<IGoal, 'id'> | null): boolean {
    return o1 && o2 ? this.getGoalIdentifier(o1) === this.getGoalIdentifier(o2) : o1 === o2;
  }

  addGoalToCollectionIfMissing<Type extends Pick<IGoal, 'id'>>(
    goalCollection: Type[],
    ...goalsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const goals: Type[] = goalsToCheck.filter(isPresent);
    if (goals.length > 0) {
      const goalCollectionIdentifiers = goalCollection.map(goalItem => this.getGoalIdentifier(goalItem));
      const goalsToAdd = goals.filter(goalItem => {
        const goalIdentifier = this.getGoalIdentifier(goalItem);
        if (goalCollectionIdentifiers.includes(goalIdentifier)) {
          return false;
        }
        goalCollectionIdentifiers.push(goalIdentifier);
        return true;
      });
      return [...goalsToAdd, ...goalCollection];
    }
    return goalCollection;
  }

  protected convertDateFromClient<T extends IGoal | NewGoal | PartialUpdateGoal>(goal: T): RestOf<T> {
    return {
      ...goal,
      createdAt: goal.createdAt?.toJSON() ?? null,
      dueDate: goal.dueDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restGoal: RestGoal): IGoal {
    return {
      ...restGoal,
      createdAt: restGoal.createdAt ? dayjs(restGoal.createdAt) : undefined,
      dueDate: restGoal.dueDate ? dayjs(restGoal.dueDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestGoal>): HttpResponse<IGoal> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestGoal[]>): HttpResponse<IGoal[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
