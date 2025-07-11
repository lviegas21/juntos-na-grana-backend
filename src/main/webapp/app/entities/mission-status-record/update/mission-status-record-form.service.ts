import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IMissionStatusRecord, NewMissionStatusRecord } from '../mission-status-record.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMissionStatusRecord for edit and NewMissionStatusRecordFormGroupInput for create.
 */
type MissionStatusRecordFormGroupInput = IMissionStatusRecord | PartialWithRequiredKeyOf<NewMissionStatusRecord>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IMissionStatusRecord | NewMissionStatusRecord> = Omit<T, 'date'> & {
  date?: string | null;
};

type MissionStatusRecordFormRawValue = FormValueOf<IMissionStatusRecord>;

type NewMissionStatusRecordFormRawValue = FormValueOf<NewMissionStatusRecord>;

type MissionStatusRecordFormDefaults = Pick<NewMissionStatusRecord, 'id' | 'date'>;

type MissionStatusRecordFormGroupContent = {
  id: FormControl<MissionStatusRecordFormRawValue['id'] | NewMissionStatusRecord['id']>;
  date: FormControl<MissionStatusRecordFormRawValue['date']>;
  statusType: FormControl<MissionStatusRecordFormRawValue['statusType']>;
  mission: FormControl<MissionStatusRecordFormRawValue['mission']>;
};

export type MissionStatusRecordFormGroup = FormGroup<MissionStatusRecordFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MissionStatusRecordFormService {
  createMissionStatusRecordFormGroup(missionStatusRecord: MissionStatusRecordFormGroupInput = { id: null }): MissionStatusRecordFormGroup {
    const missionStatusRecordRawValue = this.convertMissionStatusRecordToMissionStatusRecordRawValue({
      ...this.getFormDefaults(),
      ...missionStatusRecord,
    });
    return new FormGroup<MissionStatusRecordFormGroupContent>({
      id: new FormControl(
        { value: missionStatusRecordRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      date: new FormControl(missionStatusRecordRawValue.date, {
        validators: [Validators.required],
      }),
      statusType: new FormControl(missionStatusRecordRawValue.statusType, {
        validators: [Validators.required],
      }),
      mission: new FormControl(missionStatusRecordRawValue.mission, {
        validators: [Validators.required],
      }),
    });
  }

  getMissionStatusRecord(form: MissionStatusRecordFormGroup): IMissionStatusRecord | NewMissionStatusRecord {
    return this.convertMissionStatusRecordRawValueToMissionStatusRecord(
      form.getRawValue() as MissionStatusRecordFormRawValue | NewMissionStatusRecordFormRawValue,
    );
  }

  resetForm(form: MissionStatusRecordFormGroup, missionStatusRecord: MissionStatusRecordFormGroupInput): void {
    const missionStatusRecordRawValue = this.convertMissionStatusRecordToMissionStatusRecordRawValue({
      ...this.getFormDefaults(),
      ...missionStatusRecord,
    });
    form.reset(
      {
        ...missionStatusRecordRawValue,
        id: { value: missionStatusRecordRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MissionStatusRecordFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
    };
  }

  private convertMissionStatusRecordRawValueToMissionStatusRecord(
    rawMissionStatusRecord: MissionStatusRecordFormRawValue | NewMissionStatusRecordFormRawValue,
  ): IMissionStatusRecord | NewMissionStatusRecord {
    return {
      ...rawMissionStatusRecord,
      date: dayjs(rawMissionStatusRecord.date, DATE_TIME_FORMAT),
    };
  }

  private convertMissionStatusRecordToMissionStatusRecordRawValue(
    missionStatusRecord: IMissionStatusRecord | (Partial<NewMissionStatusRecord> & MissionStatusRecordFormDefaults),
  ): MissionStatusRecordFormRawValue | PartialWithRequiredKeyOf<NewMissionStatusRecordFormRawValue> {
    return {
      ...missionStatusRecord,
      date: missionStatusRecord.date ? missionStatusRecord.date.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
