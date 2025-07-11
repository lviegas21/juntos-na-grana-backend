import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import DailyMissionResolve from './route/daily-mission-routing-resolve.service';

const dailyMissionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/daily-mission.component').then(m => m.DailyMissionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/daily-mission-detail.component').then(m => m.DailyMissionDetailComponent),
    resolve: {
      dailyMission: DailyMissionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/daily-mission-update.component').then(m => m.DailyMissionUpdateComponent),
    resolve: {
      dailyMission: DailyMissionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/daily-mission-update.component').then(m => m.DailyMissionUpdateComponent),
    resolve: {
      dailyMission: DailyMissionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default dailyMissionRoute;
