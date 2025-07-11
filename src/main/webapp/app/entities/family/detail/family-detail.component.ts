import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IFamily } from '../family.model';

@Component({
  selector: 'jhi-family-detail',
  templateUrl: './family-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class FamilyDetailComponent {
  family = input<IFamily | null>(null);

  previousState(): void {
    window.history.back();
  }
}
