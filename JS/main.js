async function fetchNews() {
  const res = await fetch('https://your-backend.com/api/news');
  const newsList = await res.json();

  const container = document.getElementById('newsContainer');
let combinedHTML = '';

for (const news of newsList) {
  const html = await fetch('components/NewsItem.html').then(res => res.text());
  const newsHTML = html
    .replace('{thumbnail}', news.thumbnail || 'assets/default-news.jpg')
    .replace('{title}', news.title)
    .replace('{content}', news.content.slice(0, 50) + '...')
    .replace('{url}', news.url);
    
  combinedHTML += newsHTML;
}
container.innerHTML = combinedHTML;
}

fetchNews();
