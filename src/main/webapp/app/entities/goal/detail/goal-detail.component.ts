import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IGoal } from '../goal.model';

@Component({
  selector: 'jhi-goal-detail',
  templateUrl: './goal-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class GoalDetailComponent {
  goal = input<IGoal | null>(null);

  previousState(): void {
    window.history.back();
  }
}
