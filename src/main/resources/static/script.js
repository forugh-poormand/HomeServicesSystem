document.addEventListener("DOMContentLoaded", () => {
  const timerElement = document.getElementById("timer");
  const captchaDisplay = document.getElementById("captcha-display");
  const messageElement = document.getElementById("message");
  const chargeForm = document.getElementById("chargeForm");
  let realCaptcha = "";
  let timerInterval;

  async function fetchCaptcha() {
    try {
      const response = await fetch("/payment/captcha");
      if (!response.ok) throw new Error("Captcha service not available.");
      realCaptcha = await response.text();
      captchaDisplay.textContent = realCaptcha;
    } catch (error) {
      messageElement.textContent = "Error loading captcha.";
      messageElement.style.color = "red";
    }
  }

  function startTimer() {
    let timeLeft = 600; // 10 minutes
    if (timerInterval) clearInterval(timerInterval);

    timerInterval = setInterval(() => {
      if (timeLeft <= 0) {
        clearInterval(timerInterval);
        messageElement.textContent = "Time expired!";
        messageElement.style.color = "red";
        chargeForm.querySelector("button").disabled = true;
        return;
      }
      timeLeft--;
      const minutes = Math.floor(timeLeft / 60);
      const seconds = timeLeft % 60;
      timerElement.textContent = `${minutes
        .toString()
        .padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`;
    }, 1000);
  }

  chargeForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const enteredCaptcha = document.getElementById("captcha-input").value;
    const amount = document.getElementById("amount").value;
    const customerId = 1; // This should be dynamic in a real app

    if (enteredCaptcha !== realCaptcha) {
      messageElement.textContent = "Invalid Captcha!";
      messageElement.style.color = "red";
      fetchCaptcha();
      return;
    }

    try {
      const response = await fetch(`/${customerId}/wallet/charge`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ amount: parseFloat(amount) }),
      });

      const result = await response.json();

      if (response.ok) {
        messageElement.textContent = `Successfully charged! Transaction ID: ${result.id}`;
        messageElement.style.color = "green";
        clearInterval(timerInterval);
        chargeForm.querySelector("button").disabled = true;
      } else {
        messageElement.textContent = `Error: ${
          result.message || "Payment failed."
        }`;
        messageElement.style.color = "red";
      }
    } catch (error) {
      messageElement.textContent = "A network error occurred.";
      messageElement.style.color = "red";
    }
  });

  fetchCaptcha();
  startTimer();
});
