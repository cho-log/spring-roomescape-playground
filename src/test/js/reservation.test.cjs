const { test } = require('node:test');
const assert = require('node:assert/strict');
const vm = require('node:vm');
const fs = require('node:fs');
const path = require('node:path');
const source = fs.readFileSync(path.join(__dirname, '../../main/resources/static/js/reservation.js'), 'utf8');

function load(response) {
  const alerts = [];
  const context = vm.createContext({
    document: { addEventListener() {} },
    fetch: async () => response,
    alert: message => alerts.push(message),
  });
  vm.runInContext(source, context);
  return { context, alerts };
}

test('생성 실패 메시지를 표시하고 입력과 편집 상태를 유지한다', async () => {
  const { context, alerts } = load({ status: 400, json: async () => ({ message: '예약자 이름은 필수입니다.' }) });
  const inputs = [{ value: '' }, { value: '2026-11-17' }, { value: '15:40' }];
  const row = { querySelectorAll: () => inputs };
  const button = { disabled: false, parentNode: { parentNode: row } };
  vm.runInContext('isEditing = true', context);
  await context.saveRow({ target: button });
  assert.deepEqual(alerts, ['예약자 이름은 필수입니다.']);
  assert.equal(vm.runInContext('isEditing', context), true);
  assert.equal(button.disabled, false);
  assert.equal(inputs[1].value, '2026-11-17');
});

test('삭제 실패 시 행을 지우지 않고 메시지를 표시한다', async () => {
  const { context, alerts } = load({ status: 404, json: async () => ({ message: '예약을 찾을 수 없습니다.' }) });
  let removed = false;
  const row = { cells: [{ textContent: '999' }], remove() { removed = true; } };
  await context.deleteRow({ target: { closest: () => row } });
  assert.equal(removed, false);
  assert.deepEqual(alerts, ['예약을 찾을 수 없습니다.']);
});

test('JSON이 없는 오류 응답에도 기본 메시지를 제공한다', async () => {
  const { context } = load({ status: 500, json: async () => { throw new Error('not json'); } });
  await assert.rejects(context.requestRead(), /잠시 후 다시 시도/);
});
