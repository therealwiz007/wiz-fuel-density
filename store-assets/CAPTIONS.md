# Wiz Fuel Density: screenshot captions and feature graphic text

From WIZ_3APPS_MASTER_PLAN.md 2.5. At least 4 screenshots at 1080 x 1920, strongest three first.
Frame each raw phone screenshot with `shot-frame.html` (caption band 360 px, under 20% of the height).
No ranking, price or call-to-action words. Hindi captions go on the hi-IN screenshots.

| # | Screen | English caption | Hindi caption |
|---|---|---|---|
| 1 | Result shown for 743 at 28.5°C | Density at 15°C in two entries | दो एंट्री में 15°C की डेंसिटी |
| 2 | Empty form with the hydrometer hero | Enter hydrometer reading and temperature | हाइड्रोमीटर रीडिंग और तापमान लिखें |
| 3 | Log card with several entries | Your last 50 readings, saved on the phone | पिछली 50 रीडिंग फ़ोन में सुरक्षित |
| 4 | Share sheet text or copied toast | Share the reading on WhatsApp | रीडिंग WhatsApp पर भेजें |
| 5 | Dark theme result | Works without internet | बिना इंटरनेट चलता है |

Alt text (140 characters at most): the English caption plus the screen name, for example
"Wiz Fuel Density result screen: 743 kg/m³ at 28.5°C gives the density at 15°C".

Feature graphic (1024 x 500, no alpha, Wiz yellow `#FFC400` background with black text, not the dark theme):
- English: "Density at 15°C. No chart hunting." (`feature-graphic.html`)
- Hindi: "15°C पर डेंसिटी, चार्ट ढूँढे बिना" (`feature-graphic-hi.html`)
- Render: "Google Chrome" --headless --hide-scrollbars --window-size=1024,500 --screenshot=out.png file://.../feature-graphic.html

Raw screenshots: taken by the owner on a phone (plan 8.7); framing happens in a later session.
