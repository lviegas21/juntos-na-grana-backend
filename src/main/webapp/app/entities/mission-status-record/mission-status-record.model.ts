import dayjs from 'dayjs/esm';
import { IDailyMission } from 'app/entities/daily-mission/daily-mission.model';
import { MissionStatusType } from 'app/entities/enumerations/mission-status-type.model';

export interface IMissionStatusRecord {
  id: number;
  date?: dayjs.Dayjs | null;
  statusType?: keyof typeof MissionStatusType | null;
  mission?: IDailyMission | null;
}

export type NewMissionStatusRecord = Omit<IMissionStatusRecord, 'id'> & { id: null };
