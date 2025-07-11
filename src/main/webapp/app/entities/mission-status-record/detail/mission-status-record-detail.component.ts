import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IMissionStatusRecord } from '../mission-status-record.model';

@Component({
  selector: 'jhi-mission-status-record-detail',
  templateUrl: './mission-status-record-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class MissionStatusRecordDetailComponent {
  missionStatusRecord = input<IMissionStatusRecord | null>(null);

  previousState(): void {
    window.history.back();
  }
}
