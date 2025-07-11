import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IWallet, NewWallet } from '../wallet.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IWallet for edit and NewWalletFormGroupInput for create.
 */
type WalletFormGroupInput = IWallet | PartialWithRequiredKeyOf<NewWallet>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IWallet | NewWallet> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type WalletFormRawValue = FormValueOf<IWallet>;

type NewWalletFormRawValue = FormValueOf<NewWallet>;

type WalletFormDefaults = Pick<NewWallet, 'id' | 'createdAt'>;

type WalletFormGroupContent = {
  id: FormControl<WalletFormRawValue['id'] | NewWallet['id']>;
  name: FormControl<WalletFormRawValue['name']>;
  balance: FormControl<WalletFormRawValue['balance']>;
  type: FormControl<WalletFormRawValue['type']>;
  icon: FormControl<WalletFormRawValue['icon']>;
  color: FormControl<WalletFormRawValue['color']>;
  description: FormControl<WalletFormRawValue['description']>;
  createdAt: FormControl<WalletFormRawValue['createdAt']>;
  owner: FormControl<WalletFormRawValue['owner']>;
};

export type WalletFormGroup = FormGroup<WalletFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class WalletFormService {
  createWalletFormGroup(wallet: WalletFormGroupInput = { id: null }): WalletFormGroup {
    const walletRawValue = this.convertWalletToWalletRawValue({
      ...this.getFormDefaults(),
      ...wallet,
    });
    return new FormGroup<WalletFormGroupContent>({
      id: new FormControl(
        { value: walletRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(walletRawValue.name, {
        validators: [Validators.required],
      }),
      balance: new FormControl(walletRawValue.balance, {
        validators: [Validators.required],
      }),
      type: new FormControl(walletRawValue.type, {
        validators: [Validators.required],
      }),
      icon: new FormControl(walletRawValue.icon),
      color: new FormControl(walletRawValue.color),
      description: new FormControl(walletRawValue.description),
      createdAt: new FormControl(walletRawValue.createdAt, {
        validators: [Validators.required],
      }),
      owner: new FormControl(walletRawValue.owner, {
        validators: [Validators.required],
      }),
    });
  }

  getWallet(form: WalletFormGroup): IWallet | NewWallet {
    return this.convertWalletRawValueToWallet(form.getRawValue() as WalletFormRawValue | NewWalletFormRawValue);
  }

  resetForm(form: WalletFormGroup, wallet: WalletFormGroupInput): void {
    const walletRawValue = this.convertWalletToWalletRawValue({ ...this.getFormDefaults(), ...wallet });
    form.reset(
      {
        ...walletRawValue,
        id: { value: walletRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): WalletFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
    };
  }

  private convertWalletRawValueToWallet(rawWallet: WalletFormRawValue | NewWalletFormRawValue): IWallet | NewWallet {
    return {
      ...rawWallet,
      createdAt: dayjs(rawWallet.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertWalletToWalletRawValue(
    wallet: IWallet | (Partial<NewWallet> & WalletFormDefaults),
  ): WalletFormRawValue | PartialWithRequiredKeyOf<NewWalletFormRawValue> {
    return {
      ...wallet,
      createdAt: wallet.createdAt ? wallet.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
