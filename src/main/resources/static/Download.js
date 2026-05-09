function download()
{
    try
    {
    const link = document.getElementById("link").value;
    const quality = document.getElementById("quality").value
    if(link === "")
    {
        document.getElementById("status").textContent = 'Please input link';
        return;
    }
    const params = new URLSearchParams({link: link, quality: quality})
    const url = `http://localhost:8080/Magpie/downloadVideo?${params.toString()}`
    window.location.href = url;
    document.getElementById("link").value = '';
    document.getElementById("status").innerHTML = 'Download started';
    }
    catch(error)
    {
        document.getElementById("status").innerHTML = 'We apologise. An error has occured';
    }
}