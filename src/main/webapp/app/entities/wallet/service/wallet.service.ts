import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IWallet, NewWallet } from '../wallet.model';

export type PartialUpdateWallet = Partial<IWallet> & Pick<IWallet, 'id'>;

type RestOf<T extends IWallet | NewWallet> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

export type RestWallet = RestOf<IWallet>;

export type NewRestWallet = RestOf<NewWallet>;

export type PartialUpdateRestWallet = RestOf<PartialUpdateWallet>;

export type EntityResponseType = HttpResponse<IWallet>;
export type EntityArrayResponseType = HttpResponse<IWallet[]>;

@Injectable({ providedIn: 'root' })
export class WalletService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/wallets');

  create(wallet: NewWallet): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(wallet);
    return this.http
      .post<RestWallet>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(wallet: IWallet): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(wallet);
    return this.http
      .put<RestWallet>(`${this.resourceUrl}/${this.getWalletIdentifier(wallet)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(wallet: PartialUpdateWallet): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(wallet);
    return this.http
      .patch<RestWallet>(`${this.resourceUrl}/${this.getWalletIdentifier(wallet)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestWallet>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestWallet[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getWalletIdentifier(wallet: Pick<IWallet, 'id'>): number {
    return wallet.id;
  }

  compareWallet(o1: Pick<IWallet, 'id'> | null, o2: Pick<IWallet, 'id'> | null): boolean {
    return o1 && o2 ? this.getWalletIdentifier(o1) === this.getWalletIdentifier(o2) : o1 === o2;
  }

  addWalletToCollectionIfMissing<Type extends Pick<IWallet, 'id'>>(
    walletCollection: Type[],
    ...walletsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const wallets: Type[] = walletsToCheck.filter(isPresent);
    if (wallets.length > 0) {
      const walletCollectionIdentifiers = walletCollection.map(walletItem => this.getWalletIdentifier(walletItem));
      const walletsToAdd = wallets.filter(walletItem => {
        const walletIdentifier = this.getWalletIdentifier(walletItem);
        if (walletCollectionIdentifiers.includes(walletIdentifier)) {
          return false;
        }
        walletCollectionIdentifiers.push(walletIdentifier);
        return true;
      });
      return [...walletsToAdd, ...walletCollection];
    }
    return walletCollection;
  }

  protected convertDateFromClient<T extends IWallet | NewWallet | PartialUpdateWallet>(wallet: T): RestOf<T> {
    return {
      ...wallet,
      createdAt: wallet.createdAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restWallet: RestWallet): IWallet {
    return {
      ...restWallet,
      createdAt: restWallet.createdAt ? dayjs(restWallet.createdAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestWallet>): HttpResponse<IWallet> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestWallet[]>): HttpResponse<IWallet[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
