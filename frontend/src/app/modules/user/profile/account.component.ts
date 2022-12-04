import { Component, OnInit } from '@angular/core';
import { faChevronLeft, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { AccountService } from 'src/app/core/http/user/account.service';
import { Driver } from 'src/app/shared/models/driver.model';
import { Passenger } from 'src/app/shared/models/passenger.model';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
})
export class AccountComponent implements OnInit {
  faChevronLeft: IconDefinition = faChevronLeft;
  account!: Driver | Passenger;
  notFound: boolean = false;

  constructor(private accountService: AccountService) { }

  ngOnInit(): void {
    this.accountService
        .getAccount()
        .then((response) => {
          this.account = response.data;
          console.log(this.account)
          console.log(this.account.accountType)
        })
        .catch((err) => {
          this.notFound = true;
        });
  }

  getDriver(): Driver {
    return this.account as Driver;
  }

}
