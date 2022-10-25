import {Component, Input, OnInit} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';

@Component({
  selector: 'app-reactive-file-input',
  templateUrl: './reactive-file-input.component.html',
  styleUrls: ['./reactive-file-input.component.sass'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: ReactiveFileInputComponent
    }
  ]
})
export class ReactiveFileInputComponent implements OnInit, ControlValueAccessor {

  file: File | undefined;

  @Input()
  templateHref: string | undefined;

  @Input()
  accept: string | undefined;

  touched = false;

  disabled = false;

  onChange = (fileDataUrl: string | null) => {
  }

  onTouched = () => {
  }

  constructor() {
  }

  ngOnInit(): void {
  }

  onFileSelected(event: any) {
    this.file = event.target?.files[0];
    const reader = new FileReader();
    if (this.file) {
      reader.onload = () => {
        this.onChange(reader.result?.toString() ?? null);
      };
      reader.readAsDataURL(this.file);
    }
  }

  registerOnChange(onChange: any): void {
    this.onChange = onChange;
  }

  registerOnTouched(onTouched: any): void {
    this.onTouched = onTouched;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  writeValue(file: File | string): void {
    if (file instanceof File) {
      this.file = file;
    }
    if (typeof file === 'string') {
      this.file = this.dataURItoFile(file, 'Plik');
    }
  }

  markAsTouched() {
    if (!this.touched) {
      this.onTouched();
      this.touched = true;
    }
  }

  dataURItoFile(dataURI: string, fileName: string) {
    const byteString = atob(dataURI.split(',')[1]);
    const arrayBuffer = new ArrayBuffer(byteString.length);
    const int8Array = new Uint8Array(arrayBuffer);
    for (let i = 0; i < byteString.length; i++) {
      int8Array[i] = byteString.charCodeAt(i);
    }
    const typeStr = dataURI.substring(dataURI.indexOf(':') + 1, dataURI.indexOf(';'));
    const blob = new Blob([int8Array], {type: typeStr});
    return new File([blob], fileName, {type: typeStr});
  }
}
