import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMissionStatusRecord } from '../mission-status-record.model';
import { MissionStatusRecordService } from '../service/mission-status-record.service';

const missionStatusRecordResolve = (route: ActivatedRouteSnapshot): Observable<null | IMissionStatusRecord> => {
  const id = route.params.id;
  if (id) {
    return inject(MissionStatusRecordService)
      .find(id)
      .pipe(
        mergeMap((missionStatusRecord: HttpResponse<IMissionStatusRecord>) => {
          if (missionStatusRecord.body) {
            return of(missionStatusRecord.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default missionStatusRecordResolve;
