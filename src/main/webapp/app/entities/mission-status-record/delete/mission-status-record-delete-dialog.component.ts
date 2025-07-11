import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMissionStatusRecord } from '../mission-status-record.model';
import { MissionStatusRecordService } from '../service/mission-status-record.service';

@Component({
  templateUrl: './mission-status-record-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MissionStatusRecordDeleteDialogComponent {
  missionStatusRecord?: IMissionStatusRecord;

  protected missionStatusRecordService = inject(MissionStatusRecordService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.missionStatusRecordService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
