import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFamily } from '../family.model';
import { FamilyService } from '../service/family.service';

const familyResolve = (route: ActivatedRouteSnapshot): Observable<null | IFamily> => {
  const id = route.params.id;
  if (id) {
    return inject(FamilyService)
      .find(id)
      .pipe(
        mergeMap((family: HttpResponse<IFamily>) => {
          if (family.body) {
            return of(family.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default familyResolve;
