import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFamily, NewFamily } from '../family.model';

export type PartialUpdateFamily = Partial<IFamily> & Pick<IFamily, 'id'>;

type RestOf<T extends IFamily | NewFamily> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

export type RestFamily = RestOf<IFamily>;

export type NewRestFamily = RestOf<NewFamily>;

export type PartialUpdateRestFamily = RestOf<PartialUpdateFamily>;

export type EntityResponseType = HttpResponse<IFamily>;
export type EntityArrayResponseType = HttpResponse<IFamily[]>;

@Injectable({ providedIn: 'root' })
export class FamilyService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/families');

  create(family: NewFamily): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(family);
    return this.http
      .post<RestFamily>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(family: IFamily): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(family);
    return this.http
      .put<RestFamily>(`${this.resourceUrl}/${this.getFamilyIdentifier(family)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(family: PartialUpdateFamily): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(family);
    return this.http
      .patch<RestFamily>(`${this.resourceUrl}/${this.getFamilyIdentifier(family)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestFamily>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestFamily[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getFamilyIdentifier(family: Pick<IFamily, 'id'>): number {
    return family.id;
  }

  compareFamily(o1: Pick<IFamily, 'id'> | null, o2: Pick<IFamily, 'id'> | null): boolean {
    return o1 && o2 ? this.getFamilyIdentifier(o1) === this.getFamilyIdentifier(o2) : o1 === o2;
  }

  addFamilyToCollectionIfMissing<Type extends Pick<IFamily, 'id'>>(
    familyCollection: Type[],
    ...familiesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const families: Type[] = familiesToCheck.filter(isPresent);
    if (families.length > 0) {
      const familyCollectionIdentifiers = familyCollection.map(familyItem => this.getFamilyIdentifier(familyItem));
      const familiesToAdd = families.filter(familyItem => {
        const familyIdentifier = this.getFamilyIdentifier(familyItem);
        if (familyCollectionIdentifiers.includes(familyIdentifier)) {
          return false;
        }
        familyCollectionIdentifiers.push(familyIdentifier);
        return true;
      });
      return [...familiesToAdd, ...familyCollection];
    }
    return familyCollection;
  }

  protected convertDateFromClient<T extends IFamily | NewFamily | PartialUpdateFamily>(family: T): RestOf<T> {
    return {
      ...family,
      createdAt: family.createdAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restFamily: RestFamily): IFamily {
    return {
      ...restFamily,
      createdAt: restFamily.createdAt ? dayjs(restFamily.createdAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestFamily>): HttpResponse<IFamily> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestFamily[]>): HttpResponse<IFamily[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
