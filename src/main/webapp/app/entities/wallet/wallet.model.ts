import dayjs from 'dayjs/esm';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { WalletType } from 'app/entities/enumerations/wallet-type.model';

export interface IWallet {
  id: number;
  name?: string | null;
  balance?: number | null;
  type?: keyof typeof WalletType | null;
  icon?: string | null;
  color?: string | null;
  description?: string | null;
  createdAt?: dayjs.Dayjs | null;
  owner?: IAppUser | null;
}

export type NewWallet = Omit<IWallet, 'id'> & { id: null };
