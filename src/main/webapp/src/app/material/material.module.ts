import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {
  MatCardModule, MatDividerModule,
  MatFormFieldModule, MatGridListModule,
  MatInputModule, MatListModule, MatPaginatorModule, MatRadioModule,
  MatSelectModule,
  MatSlideToggleModule,
  MatStepperModule, MatTreeModule,
  MatExpansionModule,
  MatTableModule
} from "@angular/material";

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    MatToolbarModule,
    MatGridListModule,
    MatCardModule,
    MatStepperModule,
    MatFormFieldModule,
    MatInputModule,
    MatSlideToggleModule,
    MatSelectModule,
    MatButtonModule,
    MatListModule,
    MatTreeModule,
    MatPaginatorModule,
    MatDividerModule,
    MatRadioModule,
    MatExpansionModule,
    MatTableModule
  ],
  exports: [
    MatToolbarModule,
    MatGridListModule,
    MatCardModule,
    MatStepperModule,
    MatFormFieldModule,
    MatInputModule,
    MatSlideToggleModule,
    MatSelectModule,
    MatButtonModule,
    MatListModule,
    MatTreeModule,
    MatPaginatorModule,
    MatDividerModule,
    MatRadioModule,
    MatExpansionModule,
    MatTableModule
  ]
})
export class MaterialModule { }
