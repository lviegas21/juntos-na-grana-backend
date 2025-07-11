import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IFamily, NewFamily } from '../family.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFamily for edit and NewFamilyFormGroupInput for create.
 */
type FamilyFormGroupInput = IFamily | PartialWithRequiredKeyOf<NewFamily>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IFamily | NewFamily> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type FamilyFormRawValue = FormValueOf<IFamily>;

type NewFamilyFormRawValue = FormValueOf<NewFamily>;

type FamilyFormDefaults = Pick<NewFamily, 'id' | 'createdAt'>;

type FamilyFormGroupContent = {
  id: FormControl<FamilyFormRawValue['id'] | NewFamily['id']>;
  name: FormControl<FamilyFormRawValue['name']>;
  createdAt: FormControl<FamilyFormRawValue['createdAt']>;
};

export type FamilyFormGroup = FormGroup<FamilyFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FamilyFormService {
  createFamilyFormGroup(family: FamilyFormGroupInput = { id: null }): FamilyFormGroup {
    const familyRawValue = this.convertFamilyToFamilyRawValue({
      ...this.getFormDefaults(),
      ...family,
    });
    return new FormGroup<FamilyFormGroupContent>({
      id: new FormControl(
        { value: familyRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(familyRawValue.name, {
        validators: [Validators.required],
      }),
      createdAt: new FormControl(familyRawValue.createdAt, {
        validators: [Validators.required],
      }),
    });
  }

  getFamily(form: FamilyFormGroup): IFamily | NewFamily {
    return this.convertFamilyRawValueToFamily(form.getRawValue() as FamilyFormRawValue | NewFamilyFormRawValue);
  }

  resetForm(form: FamilyFormGroup, family: FamilyFormGroupInput): void {
    const familyRawValue = this.convertFamilyToFamilyRawValue({ ...this.getFormDefaults(), ...family });
    form.reset(
      {
        ...familyRawValue,
        id: { value: familyRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): FamilyFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
    };
  }

  private convertFamilyRawValueToFamily(rawFamily: FamilyFormRawValue | NewFamilyFormRawValue): IFamily | NewFamily {
    return {
      ...rawFamily,
      createdAt: dayjs(rawFamily.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertFamilyToFamilyRawValue(
    family: IFamily | (Partial<NewFamily> & FamilyFormDefaults),
  ): FamilyFormRawValue | PartialWithRequiredKeyOf<NewFamilyFormRawValue> {
    return {
      ...family,
      createdAt: family.createdAt ? family.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
