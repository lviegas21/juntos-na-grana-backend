import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../daily-mission.test-samples';

import { DailyMissionFormService } from './daily-mission-form.service';

describe('DailyMission Form Service', () => {
  let service: DailyMissionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DailyMissionFormService);
  });

  describe('Service methods', () => {
    describe('createDailyMissionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDailyMissionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            type: expect.any(Object),
            targetAmount: expect.any(Object),
            category: expect.any(Object),
            xpReward: expect.any(Object),
            createdAt: expect.any(Object),
            family: expect.any(Object),
          }),
        );
      });

      it('passing IDailyMission should create a new form with FormGroup', () => {
        const formGroup = service.createDailyMissionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            type: expect.any(Object),
            targetAmount: expect.any(Object),
            category: expect.any(Object),
            xpReward: expect.any(Object),
            createdAt: expect.any(Object),
            family: expect.any(Object),
          }),
        );
      });
    });

    describe('getDailyMission', () => {
      it('should return NewDailyMission for default DailyMission initial value', () => {
        const formGroup = service.createDailyMissionFormGroup(sampleWithNewData);

        const dailyMission = service.getDailyMission(formGroup) as any;

        expect(dailyMission).toMatchObject(sampleWithNewData);
      });

      it('should return NewDailyMission for empty DailyMission initial value', () => {
        const formGroup = service.createDailyMissionFormGroup();

        const dailyMission = service.getDailyMission(formGroup) as any;

        expect(dailyMission).toMatchObject({});
      });

      it('should return IDailyMission', () => {
        const formGroup = service.createDailyMissionFormGroup(sampleWithRequiredData);

        const dailyMission = service.getDailyMission(formGroup) as any;

        expect(dailyMission).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDailyMission should not enable id FormControl', () => {
        const formGroup = service.createDailyMissionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDailyMission should disable id FormControl', () => {
        const formGroup = service.createDailyMissionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
