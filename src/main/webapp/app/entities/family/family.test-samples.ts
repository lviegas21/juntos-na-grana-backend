import dayjs from 'dayjs/esm';

import { IFamily, NewFamily } from './family.model';

export const sampleWithRequiredData: IFamily = {
  id: 29258,
  name: 'cap',
  createdAt: dayjs('2025-07-11T04:19'),
};

export const sampleWithPartialData: IFamily = {
  id: 26308,
  name: 'morning',
  createdAt: dayjs('2025-07-11T06:18'),
};

export const sampleWithFullData: IFamily = {
  id: 6784,
  name: 'pointless even',
  createdAt: dayjs('2025-07-11T02:42'),
};

export const sampleWithNewData: NewFamily = {
  name: 'cram brr who',
  createdAt: dayjs('2025-07-10T16:51'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
