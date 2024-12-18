import {Component, input} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';
import {MatAnchor, MatMiniFabButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';


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
  ],
  standalone: true,
  imports: [MatAnchor, MatIcon, MatMiniFabButton]
})
export class ReactiveFileInputComponent implements ControlValueAccessor {

  file: File | undefined;

  templateHref = input<string>();

  accept = input<string>();

  touched = false;

  disabled = false;

  onChange = (fileDataUrl: string | null) => {
  }

  onTouched = () => {
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
