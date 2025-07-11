import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IFamily } from '../family.model';
import { FamilyService } from '../service/family.service';

@Component({
  templateUrl: './family-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class FamilyDeleteDialogComponent {
  family?: IFamily;

  protected familyService = inject(FamilyService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.familyService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
