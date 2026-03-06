// 简单的内存缓存实现
class Cache {
  constructor() {
    this.store = new Map();
  }

  async get(key) {
    const item = this.store.get(key);
    if (!item) return null;
    
    // 检查是否过期
    if (item.expiry && Date.now() > item.expiry) {
      this.store.delete(key);
      return null;
    }
    
    return item.value;
  }

  async set(key, value, ttlSeconds = 3600) {
    const expiry = ttlSeconds ? Date.now() + ttlSeconds * 1000 : null;
    this.store.set(key, { value, expiry });
  }

  async delete(key) {
    this.store.delete(key);
  }

  async clear() {
    this.store.clear();
  }
}

module.exports = new Cache();
