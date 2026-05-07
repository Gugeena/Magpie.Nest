function download()
{
    const link = document.getElementById("link").value;
    const params = new URLSearchParams({link: link})
    const url = `http://localhost:8080/Magpie/downloadVideo?${params.toString()}`
    window.location.href = url;
    document.getElementById("link").value = '';
    document.getElementById("status").textContent = 'wait for it...';
}