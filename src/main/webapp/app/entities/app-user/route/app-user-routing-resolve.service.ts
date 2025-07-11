import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAppUser } from '../app-user.model';
import { AppUserService } from '../service/app-user.service';

const appUserResolve = (route: ActivatedRouteSnapshot): Observable<null | IAppUser> => {
  const id = route.params.id;
  if (id) {
    return inject(AppUserService)
      .find(id)
      .pipe(
        mergeMap((appUser: HttpResponse<IAppUser>) => {
          if (appUser.body) {
            return of(appUser.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default appUserResolve;
