import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../mission-status-record.test-samples';

import { MissionStatusRecordFormService } from './mission-status-record-form.service';

describe('MissionStatusRecord Form Service', () => {
  let service: MissionStatusRecordFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MissionStatusRecordFormService);
  });

  describe('Service methods', () => {
    describe('createMissionStatusRecordFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMissionStatusRecordFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            statusType: expect.any(Object),
            mission: expect.any(Object),
          }),
        );
      });

      it('passing IMissionStatusRecord should create a new form with FormGroup', () => {
        const formGroup = service.createMissionStatusRecordFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            statusType: expect.any(Object),
            mission: expect.any(Object),
          }),
        );
      });
    });

    describe('getMissionStatusRecord', () => {
      it('should return NewMissionStatusRecord for default MissionStatusRecord initial value', () => {
        const formGroup = service.createMissionStatusRecordFormGroup(sampleWithNewData);

        const missionStatusRecord = service.getMissionStatusRecord(formGroup) as any;

        expect(missionStatusRecord).toMatchObject(sampleWithNewData);
      });

      it('should return NewMissionStatusRecord for empty MissionStatusRecord initial value', () => {
        const formGroup = service.createMissionStatusRecordFormGroup();

        const missionStatusRecord = service.getMissionStatusRecord(formGroup) as any;

        expect(missionStatusRecord).toMatchObject({});
      });

      it('should return IMissionStatusRecord', () => {
        const formGroup = service.createMissionStatusRecordFormGroup(sampleWithRequiredData);

        const missionStatusRecord = service.getMissionStatusRecord(formGroup) as any;

        expect(missionStatusRecord).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMissionStatusRecord should not enable id FormControl', () => {
        const formGroup = service.createMissionStatusRecordFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMissionStatusRecord should disable id FormControl', () => {
        const formGroup = service.createMissionStatusRecordFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
