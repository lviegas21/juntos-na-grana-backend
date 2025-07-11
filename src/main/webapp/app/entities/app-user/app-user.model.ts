import dayjs from 'dayjs/esm';
import { IFamily } from 'app/entities/family/family.model';

export interface IAppUser {
  id: number;
  username?: string | null;
  name?: string | null;
  avatar?: string | null;
  xpPoints?: number | null;
  level?: number | null;
  createdAt?: dayjs.Dayjs | null;
  family?: IFamily | null;
}

export type NewAppUser = Omit<IAppUser, 'id'> & { id: null };
