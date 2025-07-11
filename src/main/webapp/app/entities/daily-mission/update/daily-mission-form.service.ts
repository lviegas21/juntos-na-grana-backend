import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDailyMission, NewDailyMission } from '../daily-mission.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDailyMission for edit and NewDailyMissionFormGroupInput for create.
 */
type DailyMissionFormGroupInput = IDailyMission | PartialWithRequiredKeyOf<NewDailyMission>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDailyMission | NewDailyMission> = Omit<T, 'startDate' | 'endDate' | 'createdAt'> & {
  startDate?: string | null;
  endDate?: string | null;
  createdAt?: string | null;
};

type DailyMissionFormRawValue = FormValueOf<IDailyMission>;

type NewDailyMissionFormRawValue = FormValueOf<NewDailyMission>;

type DailyMissionFormDefaults = Pick<NewDailyMission, 'id' | 'startDate' | 'endDate' | 'createdAt'>;

type DailyMissionFormGroupContent = {
  id: FormControl<DailyMissionFormRawValue['id'] | NewDailyMission['id']>;
  title: FormControl<DailyMissionFormRawValue['title']>;
  description: FormControl<DailyMissionFormRawValue['description']>;
  startDate: FormControl<DailyMissionFormRawValue['startDate']>;
  endDate: FormControl<DailyMissionFormRawValue['endDate']>;
  type: FormControl<DailyMissionFormRawValue['type']>;
  targetAmount: FormControl<DailyMissionFormRawValue['targetAmount']>;
  category: FormControl<DailyMissionFormRawValue['category']>;
  xpReward: FormControl<DailyMissionFormRawValue['xpReward']>;
  createdAt: FormControl<DailyMissionFormRawValue['createdAt']>;
  family: FormControl<DailyMissionFormRawValue['family']>;
};

export type DailyMissionFormGroup = FormGroup<DailyMissionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DailyMissionFormService {
  createDailyMissionFormGroup(dailyMission: DailyMissionFormGroupInput = { id: null }): DailyMissionFormGroup {
    const dailyMissionRawValue = this.convertDailyMissionToDailyMissionRawValue({
      ...this.getFormDefaults(),
      ...dailyMission,
    });
    return new FormGroup<DailyMissionFormGroupContent>({
      id: new FormControl(
        { value: dailyMissionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(dailyMissionRawValue.title, {
        validators: [Validators.required],
      }),
      description: new FormControl(dailyMissionRawValue.description),
      startDate: new FormControl(dailyMissionRawValue.startDate, {
        validators: [Validators.required],
      }),
      endDate: new FormControl(dailyMissionRawValue.endDate, {
        validators: [Validators.required],
      }),
      type: new FormControl(dailyMissionRawValue.type, {
        validators: [Validators.required],
      }),
      targetAmount: new FormControl(dailyMissionRawValue.targetAmount),
      category: new FormControl(dailyMissionRawValue.category),
      xpReward: new FormControl(dailyMissionRawValue.xpReward, {
        validators: [Validators.required],
      }),
      createdAt: new FormControl(dailyMissionRawValue.createdAt, {
        validators: [Validators.required],
      }),
      family: new FormControl(dailyMissionRawValue.family, {
        validators: [Validators.required],
      }),
    });
  }

  getDailyMission(form: DailyMissionFormGroup): IDailyMission | NewDailyMission {
    return this.convertDailyMissionRawValueToDailyMission(form.getRawValue() as DailyMissionFormRawValue | NewDailyMissionFormRawValue);
  }

  resetForm(form: DailyMissionFormGroup, dailyMission: DailyMissionFormGroupInput): void {
    const dailyMissionRawValue = this.convertDailyMissionToDailyMissionRawValue({ ...this.getFormDefaults(), ...dailyMission });
    form.reset(
      {
        ...dailyMissionRawValue,
        id: { value: dailyMissionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): DailyMissionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startDate: currentTime,
      endDate: currentTime,
      createdAt: currentTime,
    };
  }

  private convertDailyMissionRawValueToDailyMission(
    rawDailyMission: DailyMissionFormRawValue | NewDailyMissionFormRawValue,
  ): IDailyMission | NewDailyMission {
    return {
      ...rawDailyMission,
      startDate: dayjs(rawDailyMission.startDate, DATE_TIME_FORMAT),
      endDate: dayjs(rawDailyMission.endDate, DATE_TIME_FORMAT),
      createdAt: dayjs(rawDailyMission.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertDailyMissionToDailyMissionRawValue(
    dailyMission: IDailyMission | (Partial<NewDailyMission> & DailyMissionFormDefaults),
  ): DailyMissionFormRawValue | PartialWithRequiredKeyOf<NewDailyMissionFormRawValue> {
    return {
      ...dailyMission,
      startDate: dailyMission.startDate ? dailyMission.startDate.format(DATE_TIME_FORMAT) : undefined,
      endDate: dailyMission.endDate ? dailyMission.endDate.format(DATE_TIME_FORMAT) : undefined,
      createdAt: dailyMission.createdAt ? dailyMission.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
