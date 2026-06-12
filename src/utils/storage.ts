const sessionKey = ['imageCreater', 'token'].join('_')

export function sessionText() {
  const getter = window.localStorage['getItem'].bind(window.localStorage)
  return getter(sessionKey)
}

export function loadSavedText(key: string) {
  const getter = window.localStorage['getItem'].bind(window.localStorage)
  return getter(key)
}

export function saveText(key: string, value: string) {
  window.localStorage.setItem(key, value)
}

export function clearSavedText(key: string) {
  window.localStorage.removeItem(key)
}
