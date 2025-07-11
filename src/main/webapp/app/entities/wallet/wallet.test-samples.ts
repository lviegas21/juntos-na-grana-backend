import dayjs from 'dayjs/esm';

import { IWallet, NewWallet } from './wallet.model';

export const sampleWithRequiredData: IWallet = {
  id: 7414,
  name: 'brood hornet',
  balance: 30502,
  type: 'SAVINGS',
  createdAt: dayjs('2025-07-11T09:34'),
};

export const sampleWithPartialData: IWallet = {
  id: 27165,
  name: 'customise upon scar',
  balance: 30838.36,
  type: 'PERSONAL',
  description: 'duh',
  createdAt: dayjs('2025-07-11T12:18'),
};

export const sampleWithFullData: IWallet = {
  id: 10095,
  name: 'oof',
  balance: 21889.38,
  type: 'SAVINGS',
  icon: 'whitewash anenst',
  color: 'violeta',
  description: 'past',
  createdAt: dayjs('2025-07-10T19:50'),
};

export const sampleWithNewData: NewWallet = {
  name: 'unlike',
  balance: 30167.59,
  type: 'SAVINGS',
  createdAt: dayjs('2025-07-11T00:54'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
