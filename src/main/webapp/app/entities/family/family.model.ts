import dayjs from 'dayjs/esm';

export interface IFamily {
  id: number;
  name?: string | null;
  createdAt?: dayjs.Dayjs | null;
}

export type NewFamily = Omit<IFamily, 'id'> & { id: null };
