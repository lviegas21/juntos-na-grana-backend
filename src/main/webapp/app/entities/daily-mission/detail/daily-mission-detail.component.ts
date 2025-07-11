import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IDailyMission } from '../daily-mission.model';

@Component({
  selector: 'jhi-daily-mission-detail',
  templateUrl: './daily-mission-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class DailyMissionDetailComponent {
  dailyMission = input<IDailyMission | null>(null);

  previousState(): void {
    window.history.back();
  }
}
