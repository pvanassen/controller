import {Component, OnInit} from '@angular/core';
import {StateService} from "./state.service";
import {timer} from "rxjs";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  state: string;

  constructor(
    private stateService:StateService
  ) { }

  ngOnInit() {
    timer(0, 5000)
      .subscribe(() => this.update());
  }

  update() {
    this.stateService.getState()
      .subscribe(val => this.state = val)
  }

  shutdown() {
    this.stateService.shutdown()
      .subscribe(() => this.update())
  }

  startup() {
    this.stateService.startup()
      .subscribe(() => this.update())
  }

  shutdownNow() {
    this.stateService.shutdownNow()
      .subscribe(() => this.update())
  }

  fireworks() {
    this.stateService.fireworks()
      .subscribe(() => this.update())
  }

  forceOn() {
    this.stateService.forceOn()
      .subscribe(() => this.update())
  }
}
