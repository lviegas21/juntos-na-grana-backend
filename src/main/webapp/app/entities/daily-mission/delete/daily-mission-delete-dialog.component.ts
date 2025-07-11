import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IDailyMission } from '../daily-mission.model';
import { DailyMissionService } from '../service/daily-mission.service';

@Component({
  templateUrl: './daily-mission-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class DailyMissionDeleteDialogComponent {
  dailyMission?: IDailyMission;

  protected dailyMissionService = inject(DailyMissionService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.dailyMissionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
