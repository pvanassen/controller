import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { StateComponent } from "./state/state.component";

const routes: Routes = [
  { path: 'state', component: StateComponent }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [ RouterModule ]
})

export class AppRoutingModule { }
