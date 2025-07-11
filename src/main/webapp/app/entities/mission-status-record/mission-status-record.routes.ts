import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import MissionStatusRecordResolve from './route/mission-status-record-routing-resolve.service';

const missionStatusRecordRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/mission-status-record.component').then(m => m.MissionStatusRecordComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/mission-status-record-detail.component').then(m => m.MissionStatusRecordDetailComponent),
    resolve: {
      missionStatusRecord: MissionStatusRecordResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/mission-status-record-update.component').then(m => m.MissionStatusRecordUpdateComponent),
    resolve: {
      missionStatusRecord: MissionStatusRecordResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/mission-status-record-update.component').then(m => m.MissionStatusRecordUpdateComponent),
    resolve: {
      missionStatusRecord: MissionStatusRecordResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default missionStatusRecordRoute;
