import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ImportEditorComponent} from './import-editor.component';

describe('ImportEditorComponent', () => {
  let component: ImportEditorComponent;
  let fixture: ComponentFixture<ImportEditorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ImportEditorComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ImportEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
