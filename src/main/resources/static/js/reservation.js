let isEditing = false;
const RESERVATION_API_ENDPOINT = '/reservations';

document.addEventListener('DOMContentLoaded', () => {
  document.getElementById('add-reservation').addEventListener('click', addEditableRow);
  fetchReservations();
});

function fetchReservations() {
  requestRead()
      .then(renderReservations)
      .catch(error => alert(error.message));
}

function renderReservations(data) {
  const tableBody = document.getElementById('reservation-table-body');
  tableBody.innerHTML = '';

  data.forEach(reservation => {
    const row = tableBody.insertRow();
    insertReservationRow(row, reservation);
  });
}

function insertReservationRow(row, reservation) {
  ['id', 'name', 'date', 'time'].forEach((field, index) => {
    row.insertCell(index).textContent = reservation[field];
  });

  const actionCell = row.insertCell(4);
  actionCell.appendChild(createActionButton('삭제', 'btn-danger', deleteRow));
}

function createActionButton(label, className, eventListener) {
  const button = document.createElement('button');
  button.textContent = label;
  button.classList.add('btn', className, 'mr-2');
  button.addEventListener('click', eventListener);
  return button;
}

function addEditableRow() {

  if (isEditing) return;

  const tableBody = document.getElementById('reservation-table-body');
  const row = tableBody.insertRow();
  isEditing = true;

  createEditableFieldsFor(row);
  addSaveAndCancelButtonsToRow(row);
}

function createEditableFieldsFor(row) {
  const fields = ['', createInput('text'), createInput('date'), createInput('time')];
  fields.forEach((field, index) => {
    const cell = row.insertCell(index);
    if (typeof field === 'string') {
      cell.textContent = field;
    } else {
      cell.appendChild(field);
    }
  });
}

function addSaveAndCancelButtonsToRow(row) {
  const actionCell = row.insertCell(4);
  actionCell.appendChild(createActionButton('확인', 'btn-primary', saveRow));
  actionCell.appendChild(createActionButton('취소', 'btn-secondary', () => {
    row.remove();
    isEditing = false;
  }));
}

function createInput(type) {
  const input = document.createElement('input');
  input.type = type;
  input.className = 'form-control';
  return input;
}

function saveRow(event) {
  const row = event.target.parentNode.parentNode;
  const inputs = row.querySelectorAll('input');

  const reservation = {
    name: inputs[0].value,
    date: inputs[1].value,
    time: inputs[2].value
  };

  const saveButton = event.target;
  if (saveButton.disabled) return;
  saveButton.disabled = true;
  return requestCreate(reservation)
      .then(data => updateRowWithReservationData(row, data))
      .catch(error => alert(error.message))
      .finally(() => { saveButton.disabled = false; });
}

function updateRowWithReservationData(row, data) {
  const cells = row.cells;
  cells[0].textContent = data.id;
  cells[1].textContent = data.name;
  cells[2].textContent = data.date;
  cells[3].textContent = data.time;

  cells[4].innerHTML = '';
  cells[4].appendChild(createActionButton('삭제', 'btn-danger', deleteRow));

  isEditing = false;

  for (let i = 1; i <= 3; i++) {
    const inputElement = cells[i].querySelector('input');
    if (inputElement) {
      inputElement.remove();
    }
  }
}

function deleteRow(event) {
  const row = event.target.closest('tr');
  const reservationId = row.cells[0].textContent;

  return requestDelete(reservationId)
      .then(() => row.remove())
      .catch(error => alert(error.message));
}

function requestCreate(reservation) {
  const requestOptions = {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(reservation)
  };

  return fetch(RESERVATION_API_ENDPOINT, requestOptions)
      .then(response => {
        if (response.status === 201) return response.json();
        return rejectResponse(response);
      });
}

function requestRead() {
  return fetch(RESERVATION_API_ENDPOINT)
      .then(response => {
        if (response.status === 200) return response.json();
        return rejectResponse(response);
      });
}

function requestDelete(id) {
  const requestOptions = {
    method: 'DELETE',
  };

  return fetch(`${RESERVATION_API_ENDPOINT}/${id}`, requestOptions)
      .then(response => {
        if (response.status !== 204) return rejectResponse(response);
      });
}
async function rejectResponse(response) {
  let message = '요청을 처리하지 못했습니다. 잠시 후 다시 시도해 주세요.';
  try {
    const error = await response.json();
    if (typeof error.message === 'string' && error.message) {
      message = error.message;
    }
  } catch {
    // JSON 본문이 없는 오류도 기본 안내 메시지로 처리한다.
  }
  throw new Error(message);
}
