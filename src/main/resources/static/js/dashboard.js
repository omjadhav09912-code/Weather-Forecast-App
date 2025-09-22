document.addEventListener("DOMContentLoaded", () => {
  const cityInput = document.getElementById("cityInput");
  const weeklyToggle = document.getElementById("weeklyToggle");
  const searchBtn = document.getElementById("searchBtn");
  const weatherInfo = document.getElementById("weatherInfo");
  const locationEl = document.getElementById("location");

  if (cityInput) cityInput.focus();

  async function displayWeather(data) {
    if (data.error) {
      weatherInfo.innerHTML = `<p style="color:red;">Error: ${data.error}</p>`;
      return;
    }

    let html = `
      <h3>Current weather in ${cityInput.value}</h3>
      <p><strong>Temperature:</strong> ${data.current.temperature} °C</p>
      <p><strong>Humidity:</strong> ${data.current.humidity} %</p>
      <p><strong>Description:</strong> ${data.current.description}</p>
    `;

    if (data.weekly) {
      html += `<h4>Weekly Forecast</h4>
      <table>
        <thead>
          <tr>
            <th>Date</th>
            <th>Min Temp (°C)</th>
            <th>Max Temp (°C)</th>
            <th>Description</th>
          </tr>
        </thead>
        <tbody>`;

      data.weekly.forEach((day) => {
        html += `
          <tr>
            <td>${day.date}</td>
            <td>${day.min}</td>
            <td>${day.max}</td>
            <td>${day.description}</td>
          </tr>`;
      });

      html += "</tbody></table>";
    }

    weatherInfo.innerHTML = html;
  }

  async function fetchWeatherByCity() {
    const city = cityInput.value.trim();
    if (!city) {
      alert("Please enter a city name.");
      cityInput.focus();
      return;
    }

    const weekly = weeklyToggle.checked;

    locationEl.textContent = `Searching weather for: ${city}`;
    weatherInfo.innerHTML = "";

    try {
      const response = await fetch(
        `/api/weather/by-city?city=${encodeURIComponent(city)}&weekly=${weekly}`
      );
      const data = await response.json();
      await displayWeather(data);
      locationEl.textContent = `Weather results for: ${city}`;
    } catch (error) {
      weatherInfo.innerHTML = `<p style="color:red;">Error fetching weather: ${error.message}</p>`;
      locationEl.textContent = "";
    }
  }

  searchBtn.addEventListener("click", (e) => {
    e.preventDefault();
    fetchWeatherByCity();
  });
});
