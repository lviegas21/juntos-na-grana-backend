import dayjs from 'dayjs/esm';

import { IAppUser, NewAppUser } from './app-user.model';

export const sampleWithRequiredData: IAppUser = {
  id: 19407,
  username: 'nephew gah cutover',
  name: 'excepting only tasty',
  xpPoints: 2465,
  level: 27695,
  createdAt: dayjs('2025-07-10T23:28'),
};

export const sampleWithPartialData: IAppUser = {
  id: 30630,
  username: 'trivial valiantly',
  name: 'from',
  avatar: 'kindheartedly condense',
  xpPoints: 21180,
  level: 28915,
  createdAt: dayjs('2025-07-10T19:12'),
};

export const sampleWithFullData: IAppUser = {
  id: 1902,
  username: 'judgementally',
  name: 'wriggler insist',
  avatar: 'godfather whether remark',
  xpPoints: 23030,
  level: 28583,
  createdAt: dayjs('2025-07-11T09:36'),
};

export const sampleWithNewData: NewAppUser = {
  username: 'economise hepatitis what',
  name: 'likely whose',
  xpPoints: 10160,
  level: 16677,
  createdAt: dayjs('2025-07-11T10:12'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
