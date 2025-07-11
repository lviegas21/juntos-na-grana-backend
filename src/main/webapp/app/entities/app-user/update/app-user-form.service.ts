import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IAppUser, NewAppUser } from '../app-user.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAppUser for edit and NewAppUserFormGroupInput for create.
 */
type AppUserFormGroupInput = IAppUser | PartialWithRequiredKeyOf<NewAppUser>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAppUser | NewAppUser> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type AppUserFormRawValue = FormValueOf<IAppUser>;

type NewAppUserFormRawValue = FormValueOf<NewAppUser>;

type AppUserFormDefaults = Pick<NewAppUser, 'id' | 'createdAt'>;

type AppUserFormGroupContent = {
  id: FormControl<AppUserFormRawValue['id'] | NewAppUser['id']>;
  username: FormControl<AppUserFormRawValue['username']>;
  name: FormControl<AppUserFormRawValue['name']>;
  avatar: FormControl<AppUserFormRawValue['avatar']>;
  xpPoints: FormControl<AppUserFormRawValue['xpPoints']>;
  level: FormControl<AppUserFormRawValue['level']>;
  createdAt: FormControl<AppUserFormRawValue['createdAt']>;
  family: FormControl<AppUserFormRawValue['family']>;
};

export type AppUserFormGroup = FormGroup<AppUserFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AppUserFormService {
  createAppUserFormGroup(appUser: AppUserFormGroupInput = { id: null }): AppUserFormGroup {
    const appUserRawValue = this.convertAppUserToAppUserRawValue({
      ...this.getFormDefaults(),
      ...appUser,
    });
    return new FormGroup<AppUserFormGroupContent>({
      id: new FormControl(
        { value: appUserRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      username: new FormControl(appUserRawValue.username, {
        validators: [Validators.required],
      }),
      name: new FormControl(appUserRawValue.name, {
        validators: [Validators.required],
      }),
      avatar: new FormControl(appUserRawValue.avatar),
      xpPoints: new FormControl(appUserRawValue.xpPoints, {
        validators: [Validators.required],
      }),
      level: new FormControl(appUserRawValue.level, {
        validators: [Validators.required],
      }),
      createdAt: new FormControl(appUserRawValue.createdAt, {
        validators: [Validators.required],
      }),
      family: new FormControl(appUserRawValue.family, {
        validators: [Validators.required],
      }),
    });
  }

  getAppUser(form: AppUserFormGroup): IAppUser | NewAppUser {
    return this.convertAppUserRawValueToAppUser(form.getRawValue() as AppUserFormRawValue | NewAppUserFormRawValue);
  }

  resetForm(form: AppUserFormGroup, appUser: AppUserFormGroupInput): void {
    const appUserRawValue = this.convertAppUserToAppUserRawValue({ ...this.getFormDefaults(), ...appUser });
    form.reset(
      {
        ...appUserRawValue,
        id: { value: appUserRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AppUserFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
    };
  }

  private convertAppUserRawValueToAppUser(rawAppUser: AppUserFormRawValue | NewAppUserFormRawValue): IAppUser | NewAppUser {
    return {
      ...rawAppUser,
      createdAt: dayjs(rawAppUser.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertAppUserToAppUserRawValue(
    appUser: IAppUser | (Partial<NewAppUser> & AppUserFormDefaults),
  ): AppUserFormRawValue | PartialWithRequiredKeyOf<NewAppUserFormRawValue> {
    return {
      ...appUser,
      createdAt: appUser.createdAt ? appUser.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
