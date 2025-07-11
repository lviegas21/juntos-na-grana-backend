import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDailyMission } from '../daily-mission.model';
import { DailyMissionService } from '../service/daily-mission.service';

const dailyMissionResolve = (route: ActivatedRouteSnapshot): Observable<null | IDailyMission> => {
  const id = route.params.id;
  if (id) {
    return inject(DailyMissionService)
      .find(id)
      .pipe(
        mergeMap((dailyMission: HttpResponse<IDailyMission>) => {
          if (dailyMission.body) {
            return of(dailyMission.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default dailyMissionResolve;
