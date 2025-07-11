import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'juntosnaGranaApplicationApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'family',
    data: { pageTitle: 'juntosnaGranaApplicationApp.family.home.title' },
    loadChildren: () => import('./family/family.routes'),
  },
  {
    path: 'app-user',
    data: { pageTitle: 'juntosnaGranaApplicationApp.appUser.home.title' },
    loadChildren: () => import('./app-user/app-user.routes'),
  },
  {
    path: 'wallet',
    data: { pageTitle: 'juntosnaGranaApplicationApp.wallet.home.title' },
    loadChildren: () => import('./wallet/wallet.routes'),
  },
  {
    path: 'goal',
    data: { pageTitle: 'juntosnaGranaApplicationApp.goal.home.title' },
    loadChildren: () => import('./goal/goal.routes'),
  },
  {
    path: 'daily-mission',
    data: { pageTitle: 'juntosnaGranaApplicationApp.dailyMission.home.title' },
    loadChildren: () => import('./daily-mission/daily-mission.routes'),
  },
  {
    path: 'mission-status-record',
    data: { pageTitle: 'juntosnaGranaApplicationApp.missionStatusRecord.home.title' },
    loadChildren: () => import('./mission-status-record/mission-status-record.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
