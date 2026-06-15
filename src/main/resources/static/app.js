const form = document.querySelector('#logForm');
const latestResult = document.querySelector('#latestResult');
const logsEl = document.querySelector('#logs');
const subjectSummary = document.querySelector('#subjectSummary');

form.addEventListener('submit', async (event) => {
  event.preventDefault();
  const payload = {
    subject: document.querySelector('#subject').value.trim(),
    minutes: Number(document.querySelector('#minutes').value),
    difficulty: Number(document.querySelector('#difficulty').value),
    confidence: Number(document.querySelector('#confidence').value),
    note: document.querySelector('#note').value.trim()
  };

  const res = await fetch('/api/logs', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });

  if (!res.ok) {
    const error = await res.json();
    alert(error.error || '新增失敗');
    return;
  }

  const data = await res.json();
  renderLatest(data);
  form.reset();
  document.querySelector('#minutes').value = 30;
  document.querySelector('#difficulty').value = 3;
  document.querySelector('#confidence').value = 3;
  await loadData();
});

function renderLatest(log) {
  latestResult.className = 'result-box';
  latestResult.innerHTML = `
    <div><span class="badge">${log.riskLevel}</span></div>
    <strong>${log.focusScore}</strong>
    <p>${log.suggestion}</p>
    <p><b>${log.subject}</b>｜${log.minutes} 分鐘｜難度 ${log.difficulty}｜信心 ${log.confidence}</p>
  `;
}

async function loadData() {
  const [logsRes, summaryRes] = await Promise.all([fetch('/api/logs'), fetch('/api/summary')]);
  const logs = await logsRes.json();
  const summary = await summaryRes.json();

  document.querySelector('#avgScore').textContent = summary.avgScore;
  document.querySelector('#count').textContent = summary.count;
  document.querySelector('#totalMinutes').textContent = summary.totalMinutes;

  subjectSummary.innerHTML = summary.bySubject.length ? summary.bySubject.map(item => `
    <div class="summary-item">
      <b>${item.subject}</b>：共 ${item.totalMinutes} 分鐘，平均分數 ${item.avgScore}
    </div>
  `).join('') : '<div class="empty">目前沒有科目統計。</div>';

  logsEl.innerHTML = logs.length ? logs.map(log => `
    <div class="log-item">
      <span class="badge">${log.riskLevel}</span>
      <h3>${log.subject}｜${log.focusScore} 分</h3>
      <p>${log.suggestion}</p>
      <small>${log.createdAt}｜${log.minutes} 分鐘｜難度 ${log.difficulty}｜信心 ${log.confidence}</small>
    </div>
  `).join('') : '<div class="empty">目前沒有紀錄。</div>';
}

loadData();
