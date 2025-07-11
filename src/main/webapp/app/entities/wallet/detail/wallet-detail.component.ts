import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IWallet } from '../wallet.model';

@Component({
  selector: 'jhi-wallet-detail',
  templateUrl: './wallet-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class WalletDetailComponent {
  wallet = input<IWallet | null>(null);

  previousState(): void {
    window.history.back();
  }
}
