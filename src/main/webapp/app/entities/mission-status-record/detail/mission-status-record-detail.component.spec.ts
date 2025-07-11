import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { MissionStatusRecordDetailComponent } from './mission-status-record-detail.component';

describe('MissionStatusRecord Management Detail Component', () => {
  let comp: MissionStatusRecordDetailComponent;
  let fixture: ComponentFixture<MissionStatusRecordDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MissionStatusRecordDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./mission-status-record-detail.component').then(m => m.MissionStatusRecordDetailComponent),
              resolve: { missionStatusRecord: () => of({ id: 31132 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(MissionStatusRecordDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MissionStatusRecordDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load missionStatusRecord on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MissionStatusRecordDetailComponent);

      // THEN
      expect(instance.missionStatusRecord()).toEqual(expect.objectContaining({ id: 31132 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
