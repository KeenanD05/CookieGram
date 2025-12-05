import { Injectable } from '@angular/core';

export interface BagItem {
  flavor: string;
  icing: string;
  message?: string;
  quantity: number;
  price: number;
}

@Injectable({ providedIn: 'root' })
export class BagService {
  private readonly STORAGE_KEY = 'cg_bag';

  getBag(): BagItem[] {
    return JSON.parse(localStorage.getItem(this.STORAGE_KEY) || '[]');
  }

  addToBag(item: BagItem) {
    const bag = this.getBag();
    bag.push(item);
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(bag));
  }

  clearBag() {
    localStorage.removeItem(this.STORAGE_KEY);
  }
}
