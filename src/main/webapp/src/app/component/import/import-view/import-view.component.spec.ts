import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ImportViewComponent } from './import-view.component';

describe('ImportViewComponent', () => {
  let component: ImportViewComponent;
  let fixture: ComponentFixture<ImportViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ImportViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ImportViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
