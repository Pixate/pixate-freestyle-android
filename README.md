Pixate Freestyle for Android
============================

This repo contains the *Pixate Freestyle* library project.

Pixate Freestyle is a free framework that lets you style your **native** Android views with stylesheets.

Under the hood, Pixate is a powerful resolution-independent graphics engine which translates familiar CSS markup to scalable graphics and bitmap effects. It is designed to work with your existing assets, as well as with SVG (Scalable Vector Graphics) assets that can be applied to your views just like any other image.

### Installing Pixate Freestyle

You can either clone this repo or download the package directly from the [releases section](https://github.com/Pixate/pixate-freestyle-android/releases).

### Integrating Pixate Freestyle

We've tried to make Pixate easy to integrate into any existing app, and there are a few simple steps that need to be done in order to have it integrated with your project.

1. In your project settings, make a reference to use the _PixateFreestyle_ library project.
2. Initialize Pixate in your Activity or Fragment by making a call to Pixate.init(...). For example:
   ```java
   @Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);

  // Initialize pixate for the Activity
  PixateFreestyle.init(this);
  
  // In case it's a Fragment, use:
  // PixateFreestyle.init(getActivity());

  setContentView(R.layout.main);
}
   ```
3. Create a _'default.css'_ file in your assets directory. This file will hold the styling markup for your app.

### Xamarin.Android

A module is also available (with source code) for using Pixate Freestyle on Xamarin.Android:

* [Xamarin Freestyle Module](https://github.com/Pixate/Xamarin-PixateFreestyle)

### Styling

Pixate enables you to style all Views of the same type by using the View's class name as an element name in the CSS, such as in this example:
```css
button {
  background-color: red;
}
```
However, in most cases you would style a view by its ID (the same one that was defined in the XML layout):
```css
#myButton {
  background-color: red;
}
```

The above example is a very simple one. Pixate also supports advanced styling instructions, like _nth-child_ for Lists, matrix transformations and more.

See the [Views Demo blog post](https://github.com/Pixate/pixate-freestyle-android/wiki/Styling-Views) to get a taste of what's possible.

### Supported Views

The Pixate Freestyle library is a work in progress. At the moment there are a few common views that we've added extensive support to:

1. View (generic attributes support for all views)
2. ImageView
3. TextView
4. CheckedTextView
4. Button
5. CompoundButton
5. ImageButton
6. ToggleButton
7. RadioButton
8. CheckBox
9. Spinner
10. ListView
11. GridView
12. EditText (support a non-editing mode)
13. ActionBar (not in the View's hierarchy, but almost completely supported)

### Samples

You can find a few sample projects under this repo's _Samples_ folder. These projects use Pixate Freestyle to style various components, and demonstrate the current capabilities of the framework.

## Contributing

Pixate welcomes contributions to our product. Just fork, make your changes, and submit a pull request. All contributors must sign a CLA (contributor license agreement).

To contribute on behalf of yourself, sign the individual CLA here:

 [http://www.freestyle.org/cla-individual.html](http://www.freestyle.org/cla-individual.html)

To contribute on behalf of your employer, sign the company CLA here:

 [http://www.freestyle.org/cla-company.html](http://www.freestyle.org/cla-company.html)

All contributions:

1. MUST be be licensed using the Apache License, Version 2.0
2. authors MAY retain copyright by adding their copyright notice to the appropriate flies

More information on the Apache License can be found here: [http://www.apache.org/foundation/license-faq.html](http://www.apache.org/foundation/license-faq.html)

## Mailing List & Twitter

Keep up with notifications and announcements by joining Pixate's [mailing list](http://pixatesurvey.herokuapp.com) and [follow us](http://twitter.com/PixateFreestyle) on Twitter.

## Docs

You can find the latest Pixate Freestyle documentation [here](http://pixate.github.io/pixate-freestyle-android).

