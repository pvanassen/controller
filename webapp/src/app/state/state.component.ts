import { Component, OnInit } from '@angular/core';
import {StateService} from "../state.service";
import {Observable} from "rxjs";
import { timer } from 'rxjs';

@Component({
  selector: 'app-state',
  templateUrl: './state.component.html',
  styleUrls: ['./state.component.css']
})
export class StateComponent implements OnInit {

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
}
