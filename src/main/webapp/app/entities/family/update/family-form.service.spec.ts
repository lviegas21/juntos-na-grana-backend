import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../family.test-samples';

import { FamilyFormService } from './family-form.service';

describe('Family Form Service', () => {
  let service: FamilyFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FamilyFormService);
  });

  describe('Service methods', () => {
    describe('createFamilyFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFamilyFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            createdAt: expect.any(Object),
          }),
        );
      });

      it('passing IFamily should create a new form with FormGroup', () => {
        const formGroup = service.createFamilyFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            createdAt: expect.any(Object),
          }),
        );
      });
    });

    describe('getFamily', () => {
      it('should return NewFamily for default Family initial value', () => {
        const formGroup = service.createFamilyFormGroup(sampleWithNewData);

        const family = service.getFamily(formGroup) as any;

        expect(family).toMatchObject(sampleWithNewData);
      });

      it('should return NewFamily for empty Family initial value', () => {
        const formGroup = service.createFamilyFormGroup();

        const family = service.getFamily(formGroup) as any;

        expect(family).toMatchObject({});
      });

      it('should return IFamily', () => {
        const formGroup = service.createFamilyFormGroup(sampleWithRequiredData);

        const family = service.getFamily(formGroup) as any;

        expect(family).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFamily should not enable id FormControl', () => {
        const formGroup = service.createFamilyFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFamily should disable id FormControl', () => {
        const formGroup = service.createFamilyFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
